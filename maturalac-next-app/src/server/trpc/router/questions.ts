import { z } from "zod";
import { getQuestionsByExamId } from "../../data/questions";
import { t } from "../trpc";

export const questionsRouter = t.router({
  getQuestionsByExamId: t.procedure
    .input(z.string())
    .query(async ({ input }) => {
      return await getQuestionsByExamId(input);
    }),
});
