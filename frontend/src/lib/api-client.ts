const SAGEMAKER_BASE_PATH = "/codeeditor/default/absports/3000";

export function withBasePath(path: string): string {
  if (process.env.NEXT_PUBLIC_SAGEMAKER === "1") {
    return `${SAGEMAKER_BASE_PATH}${path}`;
  }
  return path;
}

export async function fetchApi<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const token =
    typeof window !== "undefined" ? localStorage.getItem("token") : null;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...options.headers,
  };

  if (token) {
    (headers as Record<string, string>)["Authorization"] = `Bearer ${token}`;
  }

  const url = withBasePath(`/api${path}`);
  const response = await fetch(url, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({
      message: "通信エラーが発生しました",
    }));
    throw new ApiError(response.status, error.message || "エラーが発生しました");
  }

  return response.json();
}

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string
  ) {
    super(message);
  }
}
