// src/server/router/index.ts
import { createRouter } from "./context";
import superjson from "superjson";

import { exampleRouter } from "./subrouters/example";
import { authRouter } from "./subrouters/auth";
import { questionsRouter } from "./subrouters/questions";
import { examsRouter } from "./subrouters/exams";

export const appRouter = createRouter()
  .transformer(superjson)
  .merge("example.", exampleRouter)
  .merge("questions.", questionsRouter)
  .merge("exams.", examsRouter)
  .merge("auth.", authRouter);

// export type definition of API
export type AppRouter = typeof appRouter;
