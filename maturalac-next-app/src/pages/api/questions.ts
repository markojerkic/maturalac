import { NextApiRequest, NextApiResponse } from "next";
import firebaseGetAllQuestions from '../../server/firebase/questions';

const allQuestions = async (req: NextApiRequest, res: NextApiResponse) => {
  const allQuestions = await firebaseGetAllQuestions();
  res.status(200).json(allQuestions);
}

export default allQuestions;