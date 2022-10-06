import { z } from "zod";
import { getFileDownloadLink } from "./files";
import { firestore } from ".";
import { Question } from "@prisma/client";

const questionValidator = z.object({
  id: z.string(),
  question: z.string(),
  superQuestion: z.string().optional(),
  ansA: z.string(),
  ansB: z.string(),
  ansC: z.string(),
  ansD: z.string(),
  typeOfAnswer: z.number().min(0).max(3),
  correctAnswer: z.number().min(0).max(3).optional(),
  questionNumber: z.number().min(0).max(150),
  subject: z.string(),
  year: z.string(),
  audioName: z.string().optional(),
  superQuestionImage: z.string().optional(),
  imageURI: z.string().optional(),
  ansImg: z.string().optional(),
});

const formatedQuestionValidator = questionValidator.extend({
  audioDownloadUrl: z.string().url().optional(),
  imageDownloadUrl: z.string().url().optional(),
  answerImageDownloadUrl: z.string().url().optional(),
  superQuestionImageDownloadUrl: z.string().url().optional(),
});

const addDownloadUrls = async (question: Question) => {
  const [questionImageDownloadUrl, answerImageDownloadUrl] = await Promise.all([
    question.questionImageId
      ? getFileDownloadLink(question.questionImageId)
      : Promise.resolve(undefined),
    question.answerImageId
      ? getFileDownloadLink(question.answerImageId)
      : Promise.resolve(undefined),
    // question.sup
    //   ? getFileDownloadLink(question.superQuestionImage)
    //   : Promise.resolve(undefined),
    // question.audioName
    //   ? getFileDownloadLink(question.audioName, false)
    //   : Promise.resolve(undefined),
  ]);
  return {
    ...question,
    answerImageDownloadUrl,
    questionImageDownloadUrl,
  };
};

const getQuestionsBySubjectAndExam = async (subject: string, exam: string) => {
  const qs = (
    await firestore
      .collection("pitanja")
      .where("subject", "==", subject)
      .where("year", "==", exam)
      .orderBy("questionNumber")
      .get()
  ).docs.map((doc) => ({
    ...doc.data(),
    id: doc.id,
    correctAnswer: doc.data().correctAns,
  }));
  // const questions = await Promise.all(
  //   questionValidator.array().parse(qs);//.map(addDownloadUrls)

  return null;
};

export const getQuestionsByExamId = async (examId: string) => {
  const subjectExamYear = await prisma?.subjectExamYear.findUniqueOrThrow({
    where: {
      id: examId,
    },
    select: {
      subject: {
        select: {
          name: true,
        },
      },
      examYear: {
        select: {
          year: true,
        },
      },
    },
  });
  const questionsPromise = (
    await prisma?.question.findMany({
      where: {
        subjectExamYearId: examId,
      },
      orderBy: {
        questionNumber: "asc",
      },
    })
  )?.map(addDownloadUrls);
  if (!questionsPromise) {
    throw Error("Questions not are undefined");
  }
  const questions = await Promise.all(questionsPromise);
  return {
    subject: subjectExamYear?.subject.name,
    examYear: subjectExamYear?.examYear.year,
    questions: questions,
  };
};

type QuestionWithImageDownloadUrls = Awaited<
  ReturnType<typeof addDownloadUrls>
>;

export { getQuestionsBySubjectAndExam, formatedQuestionValidator };
export type { Question, QuestionWithImageDownloadUrls };
