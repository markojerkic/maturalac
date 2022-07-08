import { NextApiRequest, NextApiResponse } from "next";
import firebaseGetAllQuestions from '../../server/firebase/questions';

const getAllQuestions = async (req: NextApiRequest, res: NextApiResponse) => {
  const allQuestions = await firebaseGetAllQuestions();
  res.status(200).json(allQuestions);
}

export default getAllQuestions;