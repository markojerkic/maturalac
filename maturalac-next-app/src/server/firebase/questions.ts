    import { z } from 'zod';
import { getFileDownloadLink } from './files';
import { firestore } from '.';

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

type Question = z.infer<typeof questionValidator>;

const addDownloadUrls = async (question: Question) => {
  const [imageDownloadUrl,
    answerImageDownloadUrl,
    superQuestionImageDownloadUrl,
    audioDownloadUrl] = await Promise.all([
    question.imageURI ? getFileDownloadLink(question.imageURI) : Promise.resolve(undefined),
    question.ansImg ? getFileDownloadLink(question.ansImg) : Promise.resolve(undefined),
    question.superQuestionImage ? getFileDownloadLink(question.superQuestionImage) : Promise.resolve(undefined),
    question.audioName ? getFileDownloadLink(question.audioName, false) : Promise.resolve(undefined),
  ]);
  return {
    ...question,
    audioDownloadUrl,
    answerImageDownloadUrl,
    imageDownloadUrl,
    superQuestionImageDownloadUrl,
  };
};

const getQuestionsBySubjectAndExam = async (subject: string, exam: string) => {
  const qs = (await firestore.collection('pitanja').where('subject', '==', subject)
    .where('year', '==', exam).orderBy('questionNumber')
    .get()).docs.map((doc) => ({...doc.data(), id: doc.id, correctAnswer: doc.data().correctAns}));
  const questions = await Promise.all(questionValidator.array().parse(qs).map(addDownloadUrls));

  return questions;
};

export { getQuestionsBySubjectAndExam, formatedQuestionValidator };
