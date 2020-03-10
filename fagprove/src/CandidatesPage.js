import React, { useContext, useEffect } from 'react';
import { FlexDiv } from './BasicStyles';
import { Button } from './Button';
import { Link } from 'react-router-dom';
import { IsAdminContext, superRequest } from './App';





export function CandidatesPage() {
  const adminConsumer = useContext(IsAdminContext);
  const hasPemission = adminConsumer === 'ROLE_ADMIN' || adminConsumer === 'ROLE_MANAGER';



  return (
    <div style={{width: '100%'}}>
      <FlexDiv style={{width: '100%', justifyContent: 'flex-end'}}>
        {hasPemission &&
        <Link to={'/create/user'}>
          <Button>Opprett </Button>
        </Link>
        }

      </FlexDiv>

    </div>
  )
}