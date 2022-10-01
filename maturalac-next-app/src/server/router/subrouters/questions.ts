import { z } from 'zod';
import { formatedQuestionValidator, getQuestionsBySubjectAndExam } from '../../firebase/questions';
import { createRouter } from '../context';

export const questionsRouter = createRouter()
  .query('get-questions', {
    meta: {
      openapi:
      {
        enabled: true,
        method: 'GET',
        path: '/questions',
      },
    },
    input: z.object({
      subject: z.string(),
      exam: z.string(),
    }),
    // output: formatedQuestionValidator.array(),
    async resolve({ input }) {
      return await getQuestionsBySubjectAndExam(input.subject, input.exam);
    },
  });
