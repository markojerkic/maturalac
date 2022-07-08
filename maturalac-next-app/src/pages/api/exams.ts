import { NextApiRequest, NextApiResponse } from "next";
import getPublicExamsTree from "../../server/firebase/exams";

const allSubjects = async (req: NextApiRequest, res: NextApiResponse) => {
  const allSubjects = await getPublicExamsTree();

  res.status(200).json(allSubjects);
}

export default allSubjects;