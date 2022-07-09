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

const addDownloadUrls = (question: Question) => {
  return {
    ...question,
    audioDownloadUrl: question.audioName? getFileDownloadLink(question.audioName): null,
    imageDownloadUrl: question.imageURI? getFileDownloadLink(question.imageURI): null,
    superQuestionImageDownloadUrl: question.superQuestionImage? getFileDownloadLink(question.superQuestionImage): null,
  }
}

const getQuestionsBySubjectAndExam = async (subject: string, exam: string) => {
  const qs = (await firestore.collection('pitanja').where('subject', '==', subject)
    .where('year', '==', exam).get()).docs.map(doc => doc.data());
  const questions = questionValidator.array().parse(qs).map(addDownloadUrls);

  questions.filter((q) => q.imageDownloadUrl).forEach(console.log);

  return questions;
}

export { getQuestionsBySubjectAndExam };
