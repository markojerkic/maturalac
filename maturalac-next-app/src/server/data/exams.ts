import { prisma } from "../db/client";

const getPublicExamsTree = async () => {
  const tree = await prisma.subject.findMany({
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

export { getPublicExamsTree };
