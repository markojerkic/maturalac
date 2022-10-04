import { NextApiRequest, NextApiResponse } from "next";
import openApiDocument from "../../server/router/openApi";

// Respond with our OpenAPI schema
const hander = (_req: NextApiRequest, res: NextApiResponse) => {
  res.status(200).send(openApiDocument);
};

export default hander;
