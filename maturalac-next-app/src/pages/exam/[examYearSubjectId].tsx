import { AnswerType } from "@prisma/client";
import { useRouter } from "next/router";
import { z } from "zod";
import { QuestionWithImageDownloadUrls } from "../../server/data/questions";
import { trpc } from "../../utils/trpc";
import { useState } from "react";

const pathParamValidator = z.object({
  examYearSubjectId: z.string(),
});

enum UserSelectedAnswer {
  A,
  B,
  C,
  D,
}

const ABCDAnswers: React.FC<{
  answers: { ans: UserSelectedAnswer, text: string | null }[]
}> = ({ answers }) => {
  const [selectedAnswer, setSelectedAnswer] = useState<
    UserSelectedAnswer | undefined
  >();

  const selectOrDeselectAnswer = (ans: UserSelectedAnswer) => {
    if (ans === selectedAnswer) {
      setSelectedAnswer(undefined);
    } else {
      setSelectedAnswer(ans);
    }
  };

  return (
    <div className="grid grid-flow-row grid-rows-1 space-x-1 md:grid-flow-col md:grid-rows-2">
      {answers.map(ans => (
        <div>
          <button className={`btn ${selectedAnswer !== ans.ans && 'btn-outline'} m-1 w-full`}
            onClick={() => selectOrDeselectAnswer(ans.ans)}>{ans.text}</button>
        </div>
      ))
      }
    </div >
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
      <p className="text-xl my-2 mx-auto text-center">{question}</p>
      {questionImageDownloadUrl && (
        <img
          src={questionImageDownloadUrl}
          className="max-h-[50%] w-full"
          alt="Glavna slika"
        />
      )}

      {(ansA || ansB || ansC || ansD) && answerType === AnswerType.ABCD && (
        <ABCDAnswers answers={[
          { text: ansA, ans: UserSelectedAnswer.A },
          { text: ansB, ans: UserSelectedAnswer.B },
          { text: ansC, ans: UserSelectedAnswer.C },
          { text: ansD, ans: UserSelectedAnswer.D },
        ]} />
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
