import { colors } from '@/utils'
import styled from '@emotion/styled'
import { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import { CiSearch } from 'react-icons/ci'
import { MdClose } from 'react-icons/md'
import { IUser } from './AdministrationAcc/Admin.interface'

interface UserSearchProps {
  users: IUser[] ; 
}

const StyledList = styled.ul`
  position: absolute;
  width: 100%;
  z-index: 10;
  background-color: #fcfcfc;
  border: 1px solid ${colors.borderColor};
  list-style: none;
  padding: 0;
  margin: 0;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

  li {
    padding: 10px 20px;
    cursor: pointer;
    transition: background-color 0.3s ease;

    &:hover {
      background-color: #f0f0f0;
    }
  }
`;

const CloseButton = styled.button`
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 20px;
  z-index: 20;
`;
const InputWrapper = styled.div`
  display: flex;
	align-items: center;
`;

const UserSearch: React.FC<UserSearchProps> = ({ users }) => {
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [filteredUsers, setFilteredUsers] = useState<IUser[]>([]);
  const router = useRouter();

  useEffect(() => {
    if (users && searchQuery.length >= 1) {
			const filtered = users.filter(
				(user) =>
					user &&
					typeof user === 'object' &&
					'firstName' in user &&
					'lastName' in user &&
					typeof user.firstName === 'string' &&
					typeof user.lastName === 'string' &&
					`${user.firstName} ${user.lastName}`.toLowerCase().includes(searchQuery.toLowerCase())
			);
			
      setFilteredUsers(filtered);
    } else {
      setFilteredUsers([]);
    }
  }, [searchQuery, users]);

  const handleClose = () => {
    setSearchQuery('');
    setFilteredUsers([]);
  };

  const handleUserClick = (userId: number) => {
    router.push(`/user/${userId}`);
  };

  return (
    <div style={{ position: 'relative'  }}>
			<InputWrapper >
      <input
        type="search"
        placeholder="Поиск пользователя"
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        style={{outline: 'none', padding: '15px 20px', borderRadius: '180px', width: '100%', border: `1px solid ${colors.borderColor}` , position:'relative'}}
      />
			<CiSearch style={{position: 'absolute', right: '20'}} size={20}/>
			</InputWrapper>
      {filteredUsers.length > 0 && (
        <div style={{ position: 'relative' }}>
          <StyledList>
            {filteredUsers.map((user) => (
              <li
                key={user.id}
                onClick={() => {
                  handleUserClick(user.id);
                  handleClose();
                }}
              >
                {user.name}
              </li>
            ))}
          </StyledList>
          <CloseButton onClick={handleClose}>
            <MdClose />
          </CloseButton>
        </div>
      )}
    </div>
  );
};

export default UserSearch;

