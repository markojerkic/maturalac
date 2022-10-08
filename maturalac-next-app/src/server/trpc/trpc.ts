import { createProxySSGHelpers } from "@trpc/react/ssg";
import { initTRPC, TRPCError } from "@trpc/server";
import { GetServerSidePropsContext } from "next";
import superjson from "superjson";
import { Context, createSSGContext } from "./context";
import { appRouter } from "./router";

export const t = initTRPC.context<Context>().create({
  transformer: superjson,
  errorFormatter({ shape }) {
    return shape;
  },
});

export const ssgContext = async (ctx: GetServerSidePropsContext) =>
  createProxySSGHelpers({
    router: appRouter,
    ctx: await createSSGContext(ctx),
    transformer: superjson,
  });

export const authedProcedure = t.procedure.use(({ ctx, next }) => {
  if (!ctx.session || !ctx.session.user) {
    throw new TRPCError({ code: "UNAUTHORIZED" });
  }
  return next({
    ctx: {
      ...ctx,
      // infers that `session` is non-nullable to downstream resolvers
      session: { ...ctx.session, user: ctx.session.user },
    },
  });
});
