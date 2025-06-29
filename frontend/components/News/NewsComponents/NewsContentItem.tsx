import { IContentBlock } from '@/components/Blog/ContentInterface/Content.interface'
import { formatDate } from '@/components/Listing/ListingComponents/FormatDateComponent'
import Image from "next/legacy/image"
import { FC } from 'react'
import { NewsFooter, NewsImage, NewsItem, NewsMain } from './NewsComponents.styled'
interface BlogContentProps {
  content: IContentBlock;
  onClick: () => void;
}
export const NewsContentItem:FC<BlogContentProps> = ({ content , onClick }) => {
  return (
    <NewsItem onClick={onClick}>
      <NewsImage src={content.mainImage} alt="Картинка" width={240} height={240} />
      <NewsMain>
        <h3>{content.title}</h3>
        <p>{content.description}</p>
      </NewsMain>
      <NewsFooter>
        <Image src={content.author.profilePictureUrl} alt="Аватар" width={45} height={45} 
        style={{maxWidth:'45px' , maxHeight: '45px' , borderRadius:'50%'}}/>
        <div>
          <h4>{content.author.firstName}{' '}{content.author.lastName}</h4>
          <span>{formatDate(content.publicationDate)}</span>
        </div>
      </NewsFooter>
    </NewsItem>
  );
};
