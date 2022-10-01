import { useRouter } from "next/router";
import { z } from "zod";
import { Question } from "../../server/firebase/questions";
import { trpc } from "../../utils/trpc";

const queryParamValidator = z.object({
  subject: z.string(),
  exam: z.string()
});

const Question: React.FC<{question: Question}> = ({question: {id, question}}) => {
  return (
    <>
      <p>
        <span className="font-bold">Id: </span>
        {id}
      </p>
      <p>
        <span className="font-bold">Question: </span>
        {question}
      </p>
    </>
  );
};

const Exam = () => {
  const router = useRouter();
  const query = queryParamValidator.parse(router.query);
  const {data, isLoading} = trpc.useQuery(["questions.get-questions", query]);
  if (isLoading || !data) {
    return <h2>Loading...</h2>
  }
  return (
    <div className="flex flex-col mx-2 space-y-2">
      {data.map((question) => (
        <>
          <Question question={question} />
          <hr />
        </>
      ))}
    </div>
  );
}

export default Exam;