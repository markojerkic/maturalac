import { AnswerType } from "@prisma/client";
import { useRouter } from "next/router";
import { z } from "zod";
import { QuestionWithImageDownloadUrls } from "../../server/data/questions";
import { trpc } from "../../utils/trpc";

const pathParamValidator = z.object({
  examYearSubjectId: z.string(),
});

const ABCDAnswers: React.FC<{
  ansA: string | null;
  ansB: string | null;
  ansC: string | null;
  ansD: string | null;
}> = ({ ansA, ansB, ansC, ansD }) => {
  return (
    <div className="grid grid-flow-row grid-rows-1 md:grid-flow-col md:grid-rows-2">
      {ansA && (
        <div>
          <span>A&#41; </span>
          <span>{ansA}</span>
        </div>
      )}
      {ansB && (
        <div>
          <span>B&#41; </span>
          <span>{ansB}</span>
        </div>
      )}
      {ansC && (
        <div>
          <span>B&#41; </span>
          <span>{ansC}</span>
        </div>
      )}
      {ansD && (
        <div>
          <span>B&#41; </span>
          <span>{ansD}</span>
        </div>
      )}
    </div>
  );
};

const QuestionView: React.FC<{ question: QuestionWithImageDownloadUrls }> = ({
  question: {
    question,
    ansA,
    ansB,
    ansC,
    ansD,
    answerType,
    questionImageDownloadUrl,
  },
}) => {
  return (
    <div className="mx-auto my-2">
      <p>{question}</p>
      {questionImageDownloadUrl && (
        <img
          src={questionImageDownloadUrl}
          className="max-h-[50%] w-full"
          alt="Glavna slika"
        />
      )}

      {(ansA || ansB || ansC || ansD) && answerType === AnswerType.ABCD && (
        <ABCDAnswers ansA={ansA} ansB={ansB} ansC={ansC} ansD={ansD} />
      )}
    </div>
  );
};

const Exam = () => {
  const router = useRouter();
  const examId = pathParamValidator.parse(router.query).examYearSubjectId;
  const { data, isLoading } =
    trpc.question.getQuestionsByExamId.useQuery(examId);
  if (
    isLoading ||
    !data ||
    !data.examYear ||
    !data.subject ||
    !data.questions
  ) {
    return <h2>Loading...</h2>;
  }
  return (
    <div className="mx-auto flex w-[90%] flex-col space-y-2 md:w-[50%]">
      <p className="mx-auto my-4 text-2xl font-bold">
        {data.subject}: {data.examYear}
      </p>
      {data.questions.map((question) => (
        <div key={question.id}>
          <QuestionView question={question} />
          <hr />
        </div>
      ))}
    </div>
  );
};

export default Exam;
