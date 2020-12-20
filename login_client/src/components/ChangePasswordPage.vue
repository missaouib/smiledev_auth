<template>
    <div>
       <div>
       <div> 
            <form>
                Password : <input v-model="password" type="password" id="password" name="password" placeholder="Password">
                <br>
                confirm Password : <input v-model="cpassword" type="password" name="cpassword" placeholder="Password">
            </form>
            <br>
        <button @click="submit" value="submit">submit</button></div>
        <div v-if="message!=null">{{message}}</div>
    </div>
    </div>
</template>
<style>
    form{
        display: inline;
    }
</style>

<script>
import axios from "axios"
export default {
    methods:{
        submit(){
            if(this.password!=this.cpassword){
                this.message="재확인 비밀번호와 비밀번호가 다릅니다."
                return;
            }
            this.message=null;
            
            axios({
                method:"POST",
                url:"http://localhost:8001/changePassword",
                data:{
                    certifyToken:localStorage.getItem("certiToken"),
                    password:this.password
                },
            })
            .then((res)=>{
                console.log(res.data);
                if(res.data.code==300){//토큰이 유효하지 않으면
                    this.message=res.data.message;
                }else{//성공시
                    alert("비밀번호 변경이 완료되었습니다.")
                    window.location.href="/#/"
                }
            })
            .catch({
                function (error) {
                    console.log(error+"error");
                }
            })
        },
        isEmail(asValue) {
            var regExp = /^[0-9a-zA-Z]([-_]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
            return regExp.test(asValue); // 형식에 맞는 경우 true 리턴	
        }
    },
    data(){
        return{
            message:null,
            checkEmailValidate:null
        }
    }
}
</script>