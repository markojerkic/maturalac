import { z } from 'zod';
import { getPublicExamsTree, formatedSubjectValidator } from '../../firebase/exams';
import { createRouter } from '../context';

export const examsRouter = createRouter()
  .query('get-subject-exams-tree', {
    meta: {
      openapi:
      {
        enabled: true,
        method: 'GET',
        path: '/exams',
      },
    },
    input: z.void(),
    output: formatedSubjectValidator.array(),
    async resolve() {
      return await getPublicExamsTree();
    },
  });
