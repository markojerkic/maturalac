import firestore from "./firestore";

const getAllQuestions = async () => {
  const questionsSnapshot = await firestore.collection('pitanja').get();
  console.log(questionsSnapshot.docs);
  return questionsSnapshot.docs.map(doc => doc.data());
};

export default getAllQuestions;
