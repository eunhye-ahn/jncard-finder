export interface ApiError {
    status: number;
    code: string;
    message: string;
}

export class AppError extends Error {

}