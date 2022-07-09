import { z } from "zod";
import getPublicExamsTree from "../firebase/exams";
import { createRouter } from "./context";

export const examsRouter = createRouter()
  .query("get-subject-exams-tree", {
    meta: { 
      openapi: 
      { 
        enabled: true, 
        method: 'GET', 
        path: '/exams-trpc' 
      } 
    },
    input: z.void(),
    output: z.object({
      id: z.string(),
      subject: z.string(),
      exams: z.string().array()
    }).array(),
    async resolve() {
      return await getPublicExamsTree();
    },
  });
  