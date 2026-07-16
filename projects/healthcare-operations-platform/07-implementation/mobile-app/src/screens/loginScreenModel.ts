import type { LocalAuthService, LoginRequest } from "../auth/localAuth";

export type LoginScreenModel = {
  title: string;
  submit: (request: LoginRequest) => void;
};

export function createLoginScreenModel(
  authService: LocalAuthService,
  onAuthenticated: () => void,
): LoginScreenModel {
  return {
    title: "Healthcare Operations Platform",
    submit: (request) => {
      authService.login(request);
      onAuthenticated();
    },
  };
}
