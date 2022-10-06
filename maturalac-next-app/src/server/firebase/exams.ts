import { z } from "zod";

const subjectValidator = z.object({
  id: z.string(),
  subject: z.string(),
  allowed: z.boolean().array(),
  exams: z.string().array(),
});

const formatedSubjectValidator = subjectValidator.omit({ allowed: true });

type Subject = z.infer<typeof subjectValidator>;

const hasPublicExam = (subject: Subject) =>
  subject.allowed.some((allowed) => allowed);

const getPublicExamsTree = async () => {
  const tree = await prisma?.subject.findMany({
    select: {
      name: true,
      examYears: {
        select: {
          id: true,
          examYear: {
            select: {
              year: true,
            },
          },
        },
      },
    },
    where: {
      examYears: {
        some: {
          isPublic: false,
        },
      },
    },
  });
  return tree;
};

export { getPublicExamsTree, formatedSubjectValidator };
