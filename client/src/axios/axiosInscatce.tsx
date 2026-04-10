import axios from "axios";

//create생성 -기본 url, 쿠키 자동설정
export const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        'Content-Type': 'application/json'
    },
    //쿠키설정 나중에
})

//interceptor(res/req)


