import { useState } from "react";
import type { LoginRequest } from "../type/auth";
import { Login } from "../axios/api";
import { useAuthStore } from "../store/authStore";
import { useNavigate } from "react-router-dom";

export const LoginPage = () => {
    const [form, setForm] = useState<LoginRequest>({
        email: "",
        password: ""
    });
    const { setAccessToken } = useAuthStore();
    const navigate = useNavigate();

    const handleLogin = (e: React.FormEvent) => {
        e.preventDefault()
        Login(form)
            .then((res) => {
                setAccessToken(res.data.accessToken)
                console.log(res.data)
                navigate("/search");
            })
            .catch((err) => {
                console.log(err)
                setForm({
                    email: "",
                    password: ""
                })
            })
    }

    return (
        <div>
            <form onSubmit={handleLogin}>
                <div>
                    <label>EMAIL</label>
                    <input type="email"
                        onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))} />
                </div>
                <div>
                    <label>PASSWORD</label>
                    <input type="password"
                        onChange={(e) => setForm((prev) => ({ ...prev, password: e.target.value }))} />
                </div>
                <button>Login</button>
                <button onClick={() => navigate("/signup")}>SignUp</button>
            </form>
        </div>
    )
}