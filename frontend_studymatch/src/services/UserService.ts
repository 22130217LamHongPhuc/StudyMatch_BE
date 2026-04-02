import { BASE_URL } from "../config/BaseConfig"

type FormLogin = {
    email: string,
    password: string
}
export const loginRequest = async (form: FormLogin) => {
    const url = BASE_URL + '/users/login'
    console.log(url)
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
            email: form.email,
            password: form.password
        })
    })
        .then(res => {
            console.log(res, 'res nhận được')
            if (!res.ok) {
                throw new Error('Request failed');
            }
            return res.json();
        })
        .then(data => {
            console.log(data);
        })
        .catch(err => {
            console.error(err);
        });

}