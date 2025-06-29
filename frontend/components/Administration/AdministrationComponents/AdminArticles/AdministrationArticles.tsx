import { useState } from 'react'
import { AdminArticlesBody } from './AdminContent/AdminArticlesBody'
import { AdminArticlesTabs } from './AdminContent/AdminArticlesTabs'
export const AdministrationArticles = () => {
	const [activeTab, setActiveTab] = useState(0)
	return (
		<div>
			<AdminArticlesTabs setActiveTab={setActiveTab} activeTab={activeTab} />
			<AdminArticlesBody activeTab={activeTab} />
		</div>
	)
}
