import { z } from "zod";

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

export { questionValidator, formatedQuestionValidator };

