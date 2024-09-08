import React from 'react';
import { Link, Outlet } from 'react-router-dom';

/**
 * UserProfile component displays the user's personal information and provides navigation
 * to the user's ads and favorites sections.
 *
 * @param {Object} props - The component props. The object being passed from parent component.
 * @param {Object} props.user - The user property inside the props object, contains user details.
 */
function UserProfile({ user }) {
  return (
    <div>
      <h2>My Data</h2>
      <p><span style={{ textDecoration: 'underline', fontWeight: 'bold', color: 'darkred' }}>Username:</span> {user.username}</p>
      <p><span style={{ textDecoration: 'underline', fontWeight: 'bold', color: 'darkred' }}>Display Name:</span> {user.display_name}</p>
      <p><span style={{ textDecoration: 'underline', fontWeight: 'bold', color: 'darkred' }}>Email:</span> {user.email}</p>
      <p><span style={{ textDecoration: 'underline', fontWeight: 'bold', color: 'darkred' }}>Phone:</span> {user.phone}</p>
      <nav>
        <ul>
          <li style={{ marginTop: '20px' }}><Link to="MyAds">My ads</Link></li>
          <li style={{ marginTop: '20px' }}><Link to="MyFavorites">My favorites</Link></li>
        </ul>
      </nav>
      <Outlet />
    </div>
  );
}

export default UserProfile;
