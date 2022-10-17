import { AnswerType } from "@prisma/client";
import { useRouter } from "next/router";
import { z } from "zod";
import { QuestionWithImageDownloadUrls } from "../../server/data/questions";
import { trpc } from "../../utils/trpc";
import {useState} from "react";

const pathParamValidator = z.object({
  examYearSubjectId: z.string(),
});

enum SelectedAnswer {
    A, B, C, D
};


const ABCDAnswers: React.FC<{
  ansA: string | null;
  ansB: string | null;
  ansC: string | null;
  ansD: string | null;
}> = ({ ansA, ansB, ansC, ansD }) => {
  const [selectedAnswer, setSelectedAnswer] = useState<SelectedAnswer | undefined>();

  const selectOrDeselectAnswer = (ans: SelectedAnswer) => {
    if (ans === selectedAnswer) {
      setSelectedAnswer(undefined);
    } else {
      setSelectedAnswer(ans);
    }
  }


  return (
    <div className="grid grid-flow-row grid-rows-1 space-x-1 md:grid-flow-col md:grid-rows-2">
      {ansA && (
        <div>
          <button className={`btn ${selectedAnswer !== SelectedAnswer.A && 'btn-outline'} w-full m-1`} onClick={() => selectOrDeselectAnswer(SelectedAnswer.A)} >{ansA}</button>
        </div>
      )}
      {ansB && (
        <div>
          <button className={`btn ${selectedAnswer !== SelectedAnswer.B && 'btn-outline'} w-full m-1`} onClick={() => selectOrDeselectAnswer(SelectedAnswer.B)} >{ansB}</button>
        </div>
      )}
      {ansC && (
        <div>
          <button className={`btn ${selectedAnswer !== SelectedAnswer.C   && 'btn-outline'} w-full m-1`} onClick={() => selectOrDeselectAnswer(SelectedAnswer.C)} >{ansC}</button>
        </div>
      )}
      {ansD && (
        <div>
          <button className={`btn ${selectedAnswer !== SelectedAnswer.D && 'btn-outline'} w-full m-1`} onClick={() => selectOrDeselectAnswer(SelectedAnswer.D)} >{ansD}</button>
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
