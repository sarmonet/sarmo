import { useTranslation } from 'next-i18next'
import { useState } from 'react'
import { IoIosArrowUp } from "react-icons/io"
import { Title } from '../Title/Title'
import { QuestionsItem, QuestionsItems, QuestionsWrapper } from './Questions.style'
interface IQuestions {
	id: number,
	title: string;
	answer: string;
}

export const Questions = () => {
	const [activeId , setActiveId] = useState<number | null>()
	const { t } = useTranslation('common')
	const QuestionsList: IQuestions[] = [
    {
      id: 1,
      title: t('questions.questTitle1'),
      answer: t('questions.quest1'),
    },
    {
      id: 2,
      title: t('questions.questTitle2'), 
      answer: t('questions.quest2')
    },
    {
      id: 3,
      title: t('questions.questTitle3'),
      answer: t('questions.quest3')
    },
    {
      id: 4,
      title: t('questions.questTitle4'),
      answer: t('questions.quest4') 
    },
    {
      id: 5,
      title: t('questions.questTitle5'),
      answer: t('questions.quest5')
    },
    {
      id: 6,
      title: t('questions.questTitle6'),
      answer: t('questions.quest6')
    },
    {
      id: 7,
      title: t('questions.questTitle7'),
      answer: t('questions.quest7')
    },
    {
      id: 8,
      title: t('questions.questTitle8'),
      answer: t('questions.quest8')
    },
  ];
	const handleActiveClick = (Question: IQuestions) => {
		if(activeId === Question.id){
			setActiveId(null)
		}
		else{
			setActiveId(Question.id)
		}
	}
	return(
			<QuestionsWrapper>
				<Title>{t('heroTitles.questions')}</Title>
				<QuestionsItems>
					<div className="left">
						{QuestionsList.filter((_, index) => index % 2 === 0).map((Question) => (
							<QuestionsItem
								isActive={activeId === Question.id}
								key={Question.id}
								onClick={() => handleActiveClick(Question)}
							>
								<div>
									<h4>{Question.title}</h4>
									<IoIosArrowUp />
								</div>
								{activeId === Question.id && <p>{Question.answer}</p>}
							</QuestionsItem>
						))}
					</div>

					<div className="right">
						{QuestionsList.filter((_, index) => index % 2 !== 0).map((Question) => (
							<QuestionsItem
								isActive={activeId === Question.id}
								key={Question.id}
								onClick={() => handleActiveClick(Question)}
							>
								<div>
									<h4>{Question.title}</h4>
									<IoIosArrowUp/>
								</div>
								{activeId === Question.id && <p>{Question.answer}</p>}
							</QuestionsItem>
						))}
					</div>
				</QuestionsItems>
			</QuestionsWrapper>
	)
}