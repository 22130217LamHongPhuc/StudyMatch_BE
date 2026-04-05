import { BASE_URL } from "../config/BaseConfig"

type FormLogin = {
    email: string,
    password: string
}
export const loginRequest = async (form: FormLogin) => {
    const url = BASE_URL + '/users/login'
    console.log(url)

    const res = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            email: form.email,
            password: form.password
        })
    });
    const data = await res.json();

    console.log(data);

    return data;

}