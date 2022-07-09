import { z } from "zod";
import { getFileDownloadLink } from "./files";
import {firestore} from "./firebase";

const questionValidator = z.object({
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
});

type Question = z.infer<typeof questionValidator>;

const addDownloadUrls = async (question: Question) => {
  const [imageDownloadUrl,
    superQuestionImageDownloadUrl,
    audioDownloadUrl] = await Promise.all([
      question.imageURI? getFileDownloadLink(question.imageURI): Promise.resolve(null),
      question.superQuestionImage? getFileDownloadLink(question.superQuestionImage): Promise.resolve(null),
      question.audioName? getFileDownloadLink(question.audioName, false): Promise.resolve(null),
  ]);
  return {
    ...question,
    audioDownloadUrl,
    imageDownloadUrl,
    superQuestionImageDownloadUrl,
  }
}

const getQuestionsBySubjectAndExam = async (subject: string, exam: string) => {
  const qs = (await firestore.collection('pitanja').where('subject', '==', subject)
    .where('year', '==', exam).get()).docs.map(doc => doc.data());
  const questions = await Promise.all(questionValidator.array().parse(qs).map(addDownloadUrls));

  return questions;
}

export { getQuestionsBySubjectAndExam };
