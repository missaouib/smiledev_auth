<template>
    <div>
       <div> 
            <form>
                Id : <input v-model="email" type="email" id="uid" name="uid" placeholder="Id">
                <br>
                Password : <input v-model="password" type="password" id="password" name="password" placeholder="Password">
                <br>
                confirm Password : <input v-model="cpassword" type="password" name="cpassword" placeholder="Password">
            </form>
            <br>
        <button @click="submit" value="submit">submit</button></div>
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
            if(!this.isEmail(this.email)){
                this.message="이메일 형식으로 맞춰주세요"
                return;
            }
            if(this.password!=this.cpassword){
                this.message="비밀번호가 다릅니다."
                return;
            }
            this.message=null;
            
            axios({
                method:"POST",
                url:"http://localhost:8001/join",
                data:{
                uid:this.uid,
                password:this.password
                },
            })
            .then((res)=>{
                console.log(res.data);
                if(res.data.code==201){
                this.message=res.data.message;
                }else{
                window.location.href="/"
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
            message:null
        }
    }
}
</script>