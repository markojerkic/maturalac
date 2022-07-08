import { NextApiRequest, NextApiResponse } from "next";
import { z } from "zod";
import { getQuestionsBySubjectAndExam } from '../../../server/firebase/questions';

const slugValidator = z.object({
  subject: z.string(),
  exam: z.string()
});

const findQuestionsBySubjectAndExam = async (req: NextApiRequest, res: NextApiResponse) => {
  const subjectExam = slugValidator.parse(req.query);

  const allQuestions = await getQuestionsBySubjectAndExam(subjectExam.subject, subjectExam.exam);
  res.status(200).json(allQuestions);
}

export default findQuestionsBySubjectAndExam;