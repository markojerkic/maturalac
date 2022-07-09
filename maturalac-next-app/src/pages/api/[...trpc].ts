// src/pages/api/trpc/[trpc].ts
import { createOpenApiNextHandler } from 'trpc-openapi';
import { appRouter } from "../../server/router";
import { createContext } from "../../server/router/context";

// export API handler
export default createOpenApiNextHandler({
  router: appRouter,
  createContext: createContext,
});
