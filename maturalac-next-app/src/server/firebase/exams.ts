import { z } from "zod"
import firestore from "./firestore"

const subjectValidator = z.object({
  id: z.string(),
  subject: z.string(),
  allowed: z.boolean().array(),
  exams: z.string().array(),
});

type Subject = z.infer<typeof subjectValidator>;

const hasPublicExam = (subject: Subject) => {
  return subject.allowed.some((allowed) => allowed);
}

const getPublicExamsTree = async () => {
  const allExams = subjectValidator.array().parse((await firestore.collection('dozvoljeni').get())
    .docs.map(doc => ({
      ...doc.data(),
      id: doc.id
    })));

  const subjectExamsMap = allExams.filter(hasPublicExam).map((exam) => ({
    id: exam.id,
    subject: exam.subject,
    exams: exam.exams
  }));


  return subjectExamsMap;
}

export default getPublicExamsTree;