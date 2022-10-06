import { z } from "zod";
import {
  getPublicExamsTree,
  formatedSubjectValidator,
} from "../../firebase/exams";
import { createRouter } from "../context";

export const examsRouter = createRouter().query("get-subject-exams-tree", {
  input: z.void(),
  async resolve() {
    return await getPublicExamsTree();
  },
});
