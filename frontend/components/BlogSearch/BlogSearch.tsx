// import { colors } from '@/utils'
// import styled from '@emotion/styled'
// import React, { useEffect, useState } from 'react'
// import { MdClose } from 'react-icons/md'
// import { IContent } from '../Blog/ContentInterface/Content.interface'
// interface ContentSearchProps {
//   content: IContent[];
// }

// const StyledList = styled.ul`
//   position: absolute;
//   width: 100%;
//   z-index: 10;
//   background-color: #fcfcfc;
//   border: 1px solid ${colors.borderColor};
//   list-style: none;
//   padding: 0;
//   margin: 0;
//   border-radius: 10px;
//   box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

//   li {
//     padding: 10px 20px;
//     cursor: pointer;
//     transition: background-color 0.3s ease;

//     &:hover {
//       background-color: #f0f0f0;
//     }
//   }
// `;

// const CloseButton = styled.button`
//   position: absolute;
//   top: 10px;
//   right: 10px;
//   background: none;
//   border: none;
//   cursor: pointer;
//   font-size: 20px;
//   z-index: 20;
// `;

// export const BlogSearch: React.FC<ContentSearchProps> = ({ content }) => {
//   const [searchQuery, setSearchQuery] = useState<string>('');
//   const [filteredListings, setFilteredListings] = useState<IContent[]>([]);

//   useEffect(() => {
//     if (content && searchQuery.length >= 3) {
//       const filtered = Object.values(content).filter(
//         (listing) =>
//           listing &&
//           typeof listing === 'object' &&
//           'title' in listing &&
//           typeof listing.title === 'string' &&
//           listing.title.toLowerCase().includes(searchQuery.toLowerCase())
//       ) as IContent[];
//       setFilteredListings(filtered);
//     } else {
//       setFilteredListings([]);
//     }
//   }, [searchQuery, content]);

//   const handleClose = () => {
//     setSearchQuery('');
//     setFilteredListings([]);
//   };

//   // const listingPush = (listingId: number) => {
//   //   router.push({
//   //     pathname: `/blog/${listingId}`,
//   //   });
//   // };

//   return (
//     <div style={{ position: 'relative' }}>
//       <input
//         type="search"
//         placeholder="Поиск"
//         value={searchQuery}
//         onChange={(e) => setSearchQuery(e.target.value)}
//         style={{ backgroundColor: '#fcfcfc'}}
//       />
//       {filteredListings.length > 0 && (
//         <div style={{ position: 'relative' }}>
//           <StyledList>
//             {filteredListings.map((content) => (
//               <li
//                 key={content?.id}
//                 onClick={() => {
//                   setSearchQuery('');
//                   setFilteredListings([]);
//                 }}
//               >
//                 {content?.content?.value}
//               </li>
//             ))}
//           </StyledList>
//           <CloseButton onClick={handleClose}>
//             <MdClose />
//           </CloseButton>
//         </div>
//       )}
//     </div>
//   );
// };

