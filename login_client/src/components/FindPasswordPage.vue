<template>
    <div>
       <div> 
            <form>
                Id : <input v-model="email" type="email" id="uid" name="uid" placeholder="Id">
                <br>
                <button v-if="!this.isSubmitted" @click="requestEmail">Check Email</button>
            </form>
            <br>
            <div v-if="this.isSubmitted">인증번호 : <input v-model="certifycation" type="text"></div>
            <button v-if="this.isSubmitted" @click="submit" value="submit">submit</button>
        </div>
        <div v-if="message!=null">{{message}}</div>
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
            axios({
                method:"POST",
                url:"http://localhost:8001/checkCertification",
                data:{
                    email:this.tempEmail,
                    certify:this.certifycation
                },
            })
            .then((res)=>{
                if(res.data.code==200){
                    var certificationToken = res.data.certificationToken;
                    localStorage.setItem("certiToken",certificationToken);
                    window.location.href="/#/changePassword"
                }else if(res.data.code==301){
                    this.message="인증번호가 만료되었습니다 처음부터 다시 해주세요."
                }else{
                    this.message="인증번호가 틀립니다. 다시 확인해주세요."
                }
            })
        },
        async requestEmail(){
            if(!this.isEmail(this.email)){
                this.message="이메일 형식으로 맞춰주세요"
                return;
            }
            this.message=null;
            this.tempEmail=this.email

            await axios({
                method:"POST",
                url:"http://localhost:8001/findPassword",
                data:{
                    uid:this.email,
                },
            })
            .then(async (res)=>{
                await console.log(res);
                if(res.data.code==200){
                    this.isSubmitted=true;
                    this.message=res.data.message;
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
            checkEmailValidate:null,
            isSubmitted:false,
            tempEmail:null
        }
    }
}
</script>