// src/server/router/index.ts
import superjson from "superjson";
import { createRouter } from "./context";

import { authRouter } from "./subrouters/auth";
import { examsRouter } from "./subrouters/exams";
import { questionsRouter } from "./subrouters/questions";

export const appRouter = createRouter()
  .transformer(superjson)
  .merge("questions.", questionsRouter)
  .merge("exams.", examsRouter)
  .merge("auth.", authRouter);

// export type definition of API
export type AppRouter = typeof appRouter;
