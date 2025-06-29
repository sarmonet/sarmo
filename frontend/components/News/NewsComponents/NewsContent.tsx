import { IContentBlock } from '@/components/Blog/ContentInterface/Content.interface'
import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { getNewById, getNews } from '@/services/getContent'
import { useRouter } from 'next/router'
import { useCallback, useEffect, useState } from 'react'
import { NewsContentList } from './NewsComponents.styled'
import { NewsContentItem } from './NewsContentItem'
export const BlogContent = () => {
 const [content, setContent] = useState<IContentBlock[]>([]);
 const {setBlog} = useCatalog();
 const router = useRouter();

	const fetchData = useCallback(async () => {
		const data = await getNews();
		setContent(data || []);
		return data;
	}, []);

  	const fetchNewsById = useCallback(async (id : number) => {
		const data = await getNewById(id);
		setBlog(data || []);
		router.push(`/news/${id}`);
		return data;
	}, [router, setBlog]);

	useEffect(() => {fetchData()}, [fetchData]);
  return (
    <div>
      <NewsContentList>
        {content.map((item) => (
          <li key={item.id}>
           <NewsContentItem onClick = {() => {fetchNewsById(item.id)}} content={item} />
          </li>
        ))}
      </NewsContentList>
    </div>
  );
};
