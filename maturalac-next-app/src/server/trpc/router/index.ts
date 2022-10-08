// src/server/router/index.ts
import { t } from "../trpc";

import { examsRouter } from "./exams";
import { questionsRouter } from "./questions";

export const appRouter = t.router({
  question: questionsRouter,
  exam: examsRouter,
  // auth: authRouter
});

// export type definition of API
export type AppRouter = typeof appRouter;
