import { colors, mq } from '@/utils'
import styled from '@emotion/styled'



export const LoginWrapper = styled.div`
	display:flex;
	justify-content:center;
	align-items:center
	padding: 0px;
	box-shadow: 6px 4px 4px 6px rgba(0, 0, 0, 0.1);
	border-radius: 40px;
	padding: 50px 0;
	max-width: 660px;
	margin: 0 auto;

`
export const LoginFooter = styled.div`
	span{
		color: ${colors.btnMainColor};
		text-decoration: underline;
	}

`



export const LoginHat = styled.div`
text-align:center;
margin-bottom: 40px;
	h3{
		font-size: 36px;
		font-weight: 500;
		margin-bottom: 20px;
	}
		button{
			padding: 15px;
			border-radius: 180px;
			
			border: 1px solid ${colors.btnMainColor};
		}

`
export const LoginMain = styled.div`
	display:flex;
	flex-direction: column;
	row-gap: 25px;
	width:100%;
		input {
			width:440px;
			border-radius: 10px;
			padding: 15px 20px;
			border: 1px solid #050505;
			outline: none;
			appearance: none; 
		}

	@media (max-width: 480px) {
	input{
			width:320px;
		}
	}	
`
export const LoginMainButton = styled.button`
	width: 150px;
	margin: 0 auto;
	padding: 10px 35px;
	border-radius: 10px;
	font-weight: 600;
	font-size: 18px;
	background: ${colors.blueHeroBack};
	text-transform: uppercase;
	color: ${colors.mainWhiteTextColor};
`
export const LoginSocial = styled.div`
	display: flex;
	align-items: center;
	
	${mq.tablet} {
	}

	${mq.desktop} {
	
	}
	${mq.largeDesktop} {
	}
`
export const LoginSocialButton = styled.button`
	padding: 10px ;
	border-radius: 50%;
	border: 2px solid ${colors.btnMainColor};
	${mq.tablet} {
	}

	${mq.desktop} {
	
	}
	${mq.largeDesktop} {
	}
`
