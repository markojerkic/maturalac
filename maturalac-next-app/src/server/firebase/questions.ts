import { z } from "zod";
import firestore from "./firestore";

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

const getAllQuestions = async () => {
  const questionsSnapshot = (await firestore.collection('pitanja').get()).docs.map(doc => doc.data());
  const allKeys = Array.from(new Set(
    ...questionsSnapshot.map((q) => Object.keys(q))
  ));
  return allKeys;
};

const getQuestionsBySubjectAndExam = async (subject: string, exam: string) => {
  const qs = (await firestore.collection('pitanja').where('subject', '==', subject)
    .where('year', '==', exam).get()).docs.map(doc => doc.data());
  const questions = questionValidator.array().parse(qs);

  return questions;
}

export { getAllQuestions, getQuestionsBySubjectAndExam };
