import { ServerResponse } from "http";
import Post from "../../domain/entities/Post";
import SubmitPostUseCase from "../../domain/usecases/SubmitPostUseCase";
import AppFactory from "../AppFactory";
import { ParsedRequest, Route, jsonResponseWith, textResponse } from "../router";
import GetTimelineUseCase from "../../domain/usecases/GetTimelineUseCase";
import { UserNotFoundError } from "../../domain/usecases/errors/UserNotFoundError";

const routeRegExp = /^\/users\/(.+)\/timeline$/

export default {

  handle: (request: ParsedRequest, response: ServerResponse): void => {
    const matches = request.url.match(routeRegExp)!
    const userIdParameter = matches[1]

    switch (request.method) {
      case 'GET': return getRequest(userIdParameter, response)
      case 'POST': return postRequest(userIdParameter, request, response)
    }

    textResponse(405, `Method ${request.method} not allowed!`, response)
    return
  },

  shouldHandle: (r: ParsedRequest): boolean => {
    return (r.url.match(routeRegExp) ?? false)
      && ['GET', 'POST'].includes(r.method)
  }

} as Route

function getRequest(userId: string, response: ServerResponse): void {
  try {
    const usecase = new GetTimelineUseCase(
      AppFactory.getPostRepository(),
      AppFactory.getUserRepository()
    )

    const posts: Post[] = usecase.run(userId)
    jsonResponseWith(200, posts, response)
  } catch (err: any) {
    if (err instanceof UserNotFoundError)
      return textResponse(404, 'User not found.', response)

    throw err
  }

}

function postRequest(userId: string, request: ParsedRequest, response: ServerResponse): void {
  const postText = request.requestBody.text
  const usecase = new SubmitPostUseCase(AppFactory.getPostRepository())
  const submittedPost: Post = usecase.run(userId, postText)

  jsonResponseWith(201, {
    postId: submittedPost.id,
    userId: submittedPost.userId,
    text: submittedPost.text,
    dateTime: serializeDatetime(submittedPost.dateTime)
  }, response)
}

function serializeDatetime(datetime: Date): string {
  // 2018-01-10T11:30:00Z (iso format but without milliseconds)
  return datetime.toISOString().split('.')[0] + 'Z'
}
