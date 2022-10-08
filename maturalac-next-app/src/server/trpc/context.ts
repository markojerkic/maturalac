// src/server/router/context.ts
import * as trpc from "@trpc/server";
import * as trpcNext from "@trpc/server/adapters/next";

import { prisma } from "../db/client";
// src/server/router/context.ts
import { createProxySSGHelpers } from "@trpc/react/ssg";
import { GetServerSidePropsContext } from "next";
import { Session } from "next-auth";
import superjson from "superjson";
import { getServerAuthSession } from "../common/get-server-auth-session";
import { appRouter } from "./router";

type CreateContextOptions = {
  session: Session | null;
};

export const createSSGContext = async ({
  req,
  res,
}: GetServerSidePropsContext) => {
  const ctx = { prisma, session: null };
  // const session = await getServerAuthSession({ req, res });
  // await createContextInner({
  //   session,
  // });

  return await createProxySSGHelpers({
    router: appRouter,
    ctx,
    transformer: superjson,
  });
};

/** Use this helper for:
 * - testing, so we dont have to mock Next.js' req/res
 * - trpc's `createSSGHelpers` where we don't have req/res
 **/
export const createContextInner = async (opts: CreateContextOptions) => {
  return {
    session: opts.session,
    prisma,
  };
};

/**
 * This is the actual context you'll use in your router
 * @link https://trpc.io/docs/context
 **/
export const createContext = async (
  opts: trpcNext.CreateNextContextOptions
) => {
  const { req, res } = opts;

  // Get the session from the server using the unstable_getServerSession wrapper function
  const session = await getServerAuthSession({ req, res });

  return await createContextInner({
    session,
  });
};

export type Context = trpc.inferAsyncReturnType<typeof createContext>;
