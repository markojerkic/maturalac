import { useRouter } from "next/router";
import { z } from "zod";
import {
  Question,
  QuestionWithImageDownloadUrls,
} from "../../server/firebase/questions";
import { trpc } from "../../utils/trpc";

const queryParamValidator = z.object({
  subject: z.string(),
  exam: z.string(),
});

const ABCDAnswers: React.FC<{
  ansA: string;
  ansB: string;
  ansC: string;
  ansD: string;
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
    typeOfAnswer,
    imageDownloadUrl,
  },
}) => {
  return (
    <div className="mx-auto my-2">
      <p>{question}</p>
      {imageDownloadUrl && (
        <img
          src={imageDownloadUrl}
          className="max-h-[50%] w-full"
          alt="Glavna slika"
        />
      )}

      {(ansA || ansB || ansC || ansD) && typeOfAnswer === 0 && (
        <ABCDAnswers ansA={ansA} ansB={ansB} ansC={ansC} ansD={ansD} />
      )}
    </div>
  );
};

const Exam = () => {
  const router = useRouter();
  const query = queryParamValidator.parse(router.query);
  const { data, isLoading } = trpc.useQuery(["questions.get-questions", query]);
  if (isLoading || !data) {
    return <h2>Loading...</h2>;
  }
  return (
    <div className="mx-auto flex w-[90%] flex-col space-y-2 md:w-[50%]">
      <p className="mx-auto my-4 text-2xl font-bold">
        {query.subject}: {query.exam}
      </p>
      {data.map((question) => (
        <div key={question.id}>
          <QuestionView question={question} />
          <hr />
        </div>
      ))}
    </div>
  );
};

export default Exam;
