import TokenHelper from "@/utils/TokenHelper";

export default function authHeader() {
    let user = JSON.parse(TokenHelper.getUser())
    if (user && user.data.accessToken) {
        return { Authorization: 'Bearer ' + user.data.accessToken }
    } else {
        return { };
    }
}