import styled from '@emotion/styled'
export const ProfileInvestBlock = styled.div`
	display: flex;
	flex-direction: column;
	h4 {
		margin-left: 18px;
		font-size: 18px;
		margin-bottom: 15px;
	}
	p {
		margin-left: 18px;
		font-size: 14px;
		font-weight: 600;
		margin: 0 0 20px 35px;
	}

	label {
		display: grid;
		grid-template-columns: 15px 1fr;
		align-items: center;
		margin-left: 45px;
		@media (max-width: 768px) {
			margin-left: 15px;
		}
	}
	span {
		font-size: 16px;
		max-height: 12px;
		margin-left: 10px;
		font-weight: 400;
		line-height: 1.3;
		@media (max-width: 768px) {
			max-height: none;
		}
	}
	.input {
		width: 100%;

		padding: 10px;
		box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.1);
	}
	.textarea {
		padding: 10px;
		box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.1);
	}
`
