import { getPublicExamsTree } from "../../data/exams";
import { t } from "../trpc";

export const examsRouter = t.router({
  getPublicExamsTree: t.procedure.query(async () => {
    return await getPublicExamsTree();
  }),
});
