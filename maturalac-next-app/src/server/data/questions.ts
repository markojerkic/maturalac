import { Question } from "@prisma/client";
import { prisma } from "../db/client";
import { getFileDownloadLink } from "../firebase/files";

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

const getQuestionsByExamId = async (examId: string) => {
  const subjectExamYear = await prisma.subjectExamYear.findUniqueOrThrow({
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

export { getQuestionsByExamId };
export type { QuestionWithImageDownloadUrls };
