import getPublicExamsTree from "../firebase/exams";
import { createRouter } from "./context";

export const examsRouter = createRouter()
  .query("get-subject-exams-tree", {
    async resolve() {
      return await getPublicExamsTree();
    },
  });
  