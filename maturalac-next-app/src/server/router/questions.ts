import { z } from "zod";
import { getQuestionsBySubjectAndExam } from "../firebase/questions";
import { createRouter } from "./context";

export const questionsRouter = createRouter()
  .query("get-subject-exams-tree", {
    input: z.object({
      subject: z.string(),
      exam: z.string(),
    }),
    async resolve({ input }) {
      return await getQuestionsBySubjectAndExam(input.subject, input.exam);
    },
  });
  