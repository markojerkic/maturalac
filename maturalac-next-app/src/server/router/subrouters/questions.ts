import { z } from "zod";
import { getQuestionsByExamId } from "../../data/questions";
import { createRouter } from "../context";

export const questionsRouter = createRouter().query("get-questions-by-exam", {
  input: z.string(),
  async resolve({ input }) {
    return await getQuestionsByExamId(input);
  },
});
